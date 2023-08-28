sh ./build-source.sh

# build doc
typedoc $(find . -iname '*.ts' -not -iname '*.d.ts') --out ./dist/docs
