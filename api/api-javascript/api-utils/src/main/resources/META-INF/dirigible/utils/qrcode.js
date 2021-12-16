const qrcode = require('utils/v4/qrcode');
for(const propertyName in qrcode) {
	exports[propertyName] = qrcode[propertyName];
}
