declare module core {
    module configuration {
        function get(key, defaultValue): string;

        function set(key, value): string;

        function remove(key);

        function getKeys(): string[];

        function load(path: string);

        function update();

        function getOS();

        function isOSWindows(): boolean;

        function isOSMac(): boolean;

        function isOSLinux(): boolean;

        function isOSSolaris(): boolean;
    }
    module console {
        function log(message);

        function error(message);

        function info(message);

        function warna(message);

        function debug(message);

        function trace(message);

        function stringify(message);
    }
    module context {
        function get(name): string;

        function set(name, value);
    }
    module destination {
        function get(name): string;

        function set(name, destination);
    }
    module env {
        function get(name): string;

        function list(): string[];
    }
    module exec {
        function exec(commandLine, toAdd, toRemove);
    }
    module extensions {
        function getExtensions(extensionPoint);

        function getExtensionPoints();
    }
    module globals {
        function get(name): string;

        function set(name, value);

        function list(): string[];
    }
    module threads {
        function create(runnable, name): Thread;

        function sleep(mills);

        function current(): Thread;

        function getClassObject(clazz);

        function wait(mills);

        function notify();

        function notifyAll();

    }

    interface Thread {
        internalThread();

        getInternalObject();

        start();

        interrupt();

        join();

        getID(): number;

        getName(): string;

        getAlive(): boolean;

    }
}