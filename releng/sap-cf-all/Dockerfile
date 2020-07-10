# Docker descriptor for Dirigible
# License - http://www.eclipse.org/legal/epl-v20.html

ARG DIRIGIBLE_VERSION=latest
FROM dirigiblelabs/dirigible-base-platform-sap-cf:$DIRIGIBLE_VERSION

EXPOSE 8080

CMD ["catalina.sh", "jpda", "run"]
