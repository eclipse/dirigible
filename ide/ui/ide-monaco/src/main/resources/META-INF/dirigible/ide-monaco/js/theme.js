let monacoTheme = 'vs-light';
let headElement = document.getElementsByTagName('head')[0];

function setTheme(init = true) {
    let theme = JSON.parse(localStorage.getItem('DIRIGIBLE.theme') || '{}');
    if (theme.type === 'light') monacoTheme = 'vs-light';
    else monacoTheme = 'vs-dark';
    if (theme.links) {
        if (!init) {
            let themeLinks = headElement.querySelectorAll("link[data-type='theme']");
            for (let i = 0; i < themeLinks.length; i++) {
                headElement.removeChild(themeLinks[i]);
            }
        }
        for (let i = 0; i < theme.links.length; i++) {
            const link = document.createElement('link');
            link.type = 'text/css';
            link.href = theme.links[i];
            link.rel = 'stylesheet';
            link.setAttribute("data-type", "theme");
            headElement.appendChild(link);
        }
    }
}