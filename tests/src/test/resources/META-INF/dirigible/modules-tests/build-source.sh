set -e

# clean
rm -rf ./dist

# build esm
esbuild $(find . -iname '*.ts' -not -iname '*.d.ts') '--out-extension:.js=.mjs' --sourcemap=inline --outdir=dist/esm --format=esm --target=es2022

# build cjs
esbuild $(find . -iname '*.ts' -not -iname '*.d.ts') --sourcemap=inline --bundle --external:@dirigible/* --outdir=dist/cjs --format=cjs --target=es2022