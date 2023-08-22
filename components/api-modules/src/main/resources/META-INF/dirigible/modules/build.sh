# clean
rm -rf ./dist

# build esm
esbuild $(find . -iname '*.ts' -not -iname '*.d.ts') '--out-extension:.js=.mjs' --outdir=dist/esm --format=esm --target=es2022

# build cjs
esbuild $(find . -iname '*.ts' -not -iname '*.d.ts') --bundle --outdir=dist/cjs --format=cjs --target=es2022

# build dts
tsc --emitDeclarationOnly --outDir dist/dts

# build doc
typedoc $(find . -iname '*.ts' -not -iname '*.d.ts') --out ./dist/docs