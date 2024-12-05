function getViewParameters() {
    if (window.frameElement && window.frameElement.hasAttribute('data-parameters')) {
        return JSON.parse(window.frameElement.getAttribute('data-parameters') ?? '{}');
    }
    return {};
}