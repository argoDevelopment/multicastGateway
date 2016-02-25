# Doc Writers
echo "Building the docs Writers PDF ..."
prince --javascript --input-list=../doc_outputs/docs/writers-pdf/prince-file-list.txt -o docs/files/argo_docs_writers_pdf.pdf;
echo "done"

# Doc Designers
echo "Building docs Designers PDF ..."
prince --javascript --input-list=../doc_outputs/docs/designers-pdf/prince-file-list.txt -o docs/files/argo_docs_designers_pdf.pdf;
echo "done"

echo "All done building the PDFs!"
echo "Now build the web outputs: . argo_docs_3_multibuild_web.sh"