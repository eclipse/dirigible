package org.eclipse.dirigible.runtime.listener.mail;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import org.eclipse.dirigible.repository.logging.Logger;

import com.sun.mail.iap.ProtocolException;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;
import com.sun.mail.imap.protocol.IMAPProtocol;

public class ImapsClient {

	private static final Logger logger = Logger.getLogger(ImapsClient.class);

	private Thread keepAliveThread;
	private Thread imapsIdleThread;

	protected void connect(final IMailHandler mailHandler) {

		Properties properties = new Properties();
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.imaps.host", mailHandler.getHost());
		properties.put("mail.imaps.port", mailHandler.getPort());
		properties.put("mail.imaps.timeout", mailHandler.getTimeout());
		properties.put("mail.debug", mailHandler.getDebug());

		Session session = Session.getInstance(properties);

		IMAPStore store = null;
		IMAPFolder inbox = null;

		try {
			store = (IMAPStore) session.getStore("imaps");
			store.connect(mailHandler.getUsername(), mailHandler.getPassword());

			if (!store.hasCapability("IDLE")) {
				throw new RuntimeException("IDLE not supported");
			}

			inbox = (IMAPFolder) store.getFolder(mailHandler.getFolder());

			this.keepAliveThread = new Thread(new ImapsKeepAliveRunnable(inbox), "IdleConnectionKeepAlive");
			keepAliveThread.start();

			inbox.addMessageCountListener(new MessageCountAdapter() {

				@Override
				public void messagesAdded(MessageCountEvent event) {
					Message[] messages = event.getMessages();

					for (Message message : messages) {
						try {
							logger.info("Mail received with subject: " + message.getSubject());
							mailHandler.handleMail(message);
						} catch (MessagingException e) {
							logger.error("Error in handling received messages", e);
						}
					}
				}
			});

			this.imapsIdleThread = new ImapsIdleThread(inbox, mailHandler.getUsername(), mailHandler.getPassword());
			imapsIdleThread.setDaemon(false);
			imapsIdleThread.start();

			imapsIdleThread.join();

		} catch (InterruptedException e) {
			logger.error("ImapsClient interrupted");
		} catch (Exception e) {
			logger.error("ImapsClient", e);
		} finally {
			closeFolder(inbox);
			closeStore(store);
		}
	}

	public void disconnect() {
		this.keepAliveThread.interrupt();
		((ImapsIdleThread) this.imapsIdleThread).kill();
	}

	private static class ImapsIdleThread extends Thread {
		private final Folder folder;
		private final String username;
		private final String password;
		private volatile boolean running = true;

		public ImapsIdleThread(Folder folder, String username, String password) {
			super();
			this.folder = folder;
			this.username = username;
			this.password = password;
		}

		public synchronized void kill() {

			if (!running) {
				return;
			}
			this.running = false;
		}

		@Override
		public void run() {
			while (running) {

				try {
					ensureOpen(username, password, folder);
					logger.debug("Enter idle...");
					((IMAPFolder) folder).idle();
				} catch (Exception e) {
					// logger.error("ImapsIdleThread run", e);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// ignore
					}
				}
			}
		}
	}

	public static void closeFolder(final Folder folder) {
		try {
			if ((folder != null) && folder.isOpen()) {
				folder.close(false);
			}
		} catch (final Exception e) {
			// ignore
		}
	}

	public static void closeStore(final Store store) {
		try {
			if ((store != null) && store.isConnected()) {
				store.close();
			}
		} catch (final Exception e) {
			// ignore
		}
	}

	public static void ensureOpen(final String username, final String password, final Folder folder) throws MessagingException {

		if (folder != null) {
			Store store = folder.getStore();
			if ((store != null) && !store.isConnected()) {
				store.connect(username, password);
			}
		} else {
			throw new MessagingException("Folder is null");
		}

		if (folder.exists() && !folder.isOpen() && ((folder.getType() & Folder.HOLDS_MESSAGES) != 0)) {
			logger.debug(String.format("Open folder: %s", folder.getFullName()));
			folder.open(Folder.READ_ONLY);
			if (!folder.isOpen()) {
				throw new MessagingException(String.format("Unable to open folder: %s ", folder.getFullName()));
			}
		}
	}

	private static class ImapsKeepAliveRunnable implements Runnable {

		private static final long KEEP_ALIVE_FREQ = 300000;

		private IMAPFolder folder;

		public ImapsKeepAliveRunnable(IMAPFolder folder) {
			this.folder = folder;
		}

		@Override
		public void run() {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(KEEP_ALIVE_FREQ);
					// Performing a NOOP to keep the connection alive
					folder.doCommand(new IMAPFolder.ProtocolCommand() {
						@Override
						public Object doCommand(IMAPProtocol p) throws ProtocolException {
							p.simpleCommand("NOOP", null);
							return null;
						}
					});
				} catch (InterruptedException e) {
					// ignore
				} catch (MessagingException e) {
					logger.warn("Unexpected exception while keeping alive the IDLE connection");
					logger.error("ImapsKeepAliveRunnable", e);
				}
			}
		}
	}

}
