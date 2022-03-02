const computeNewLines = (oldText, newText, isWhitespaceIgnored = true) => {
    if (oldText[oldText.length - 1] !== "\n" || newText[newText.length - 1] !== "\n") {
        oldText += "\n";
        newText += "\n"
    }
    let lineDiff;
    if (isWhitespaceIgnored) {
        lineDiff = Diff.diffTrimmedLines(oldText, newText)
    } else {
        lineDiff = Diff.diffLines(oldText, newText)
    }
    let addedCount = 0;
    let addedLines = [];
    lineDiff.forEach(part => {
        let { added, removed, value } = part;
        let count = value.split("\n").length - 1;
        if (!added && !removed) {
            addedCount += count
        }
        else
            if (added) {
                for (let i = 0; i < count; i++) {
                    addedLines.push(addedCount + i + 1)
                }
                addedCount += count
            }
    });
    return addedLines
};