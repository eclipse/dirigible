exports.generateQRCode = function(text){
    const bytes = require("io/v4/bytes");
    return bytes.toJavaScriptBytes(org.eclipse.dirigible.api.v3.utils.QRCodeFacade.generateQRCode(text));
};
