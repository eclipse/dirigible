ARG DIRIGIBLE_VERSION=latest
FROM dirigiblelabs/dirigible-sap-kyma:$DIRIGIBLE_VERSION as base

ENV CNB_USER_ID=1000
ENV CNB_GROUP_ID=1000
ENV CNB_STACK_ID="org.eclipse.dirigible"
LABEL io.buildpacks.stack.id="org.eclipse.dirigible"

RUN groupadd cnb --gid ${CNB_GROUP_ID} && \
  useradd --uid ${CNB_USER_ID} --gid ${CNB_GROUP_ID} -m -s /bin/bash cnb

RUN chmod -R 777 /usr/local/tomcat

FROM base as run

RUN chmod -R 777 /usr/local/tomcat

USER ${CNB_USER_ID}:${CNB_GROUP_ID}

FROM base as build

RUN chmod -R 777 /usr/local/tomcat

USER ${CNB_USER_ID}:${CNB_GROUP_ID}

