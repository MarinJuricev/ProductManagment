package components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RichTextEditor(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp)),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()

                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun onTextChange(text: String) {
                            onValueChanged(text)
                        }
                    },
                    INTERFACE_NAME,
                )

                loadDataWithBaseURL(
                    BASE_URL,
                    richTextEditorHtmlScript(innerText = value),
                    MIME_TYPE,
                    ENCODING,
                    HISTORY_URL,
                )
            }
        },
    )
}

private const val INTERFACE_NAME = "Android"
private val BASE_URL = null
private const val MIME_TYPE = "text/html"
private const val ENCODING = "UTF-8"
private val HISTORY_URL = null

private fun richTextEditorHtmlScript(innerText: String) = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link href="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css" rel="stylesheet">
            <style>
                body { margin: 0; padding: 12px; background-color: transparent; }
                #editor-container {
                    border: 1px solid #ccc;
                    border-radius: 8px;
                    overflow: hidden;
                }
                
                .ql-toolbar {
                    border: none !important;
                    border-bottom: 1px solid #ccc !important;
                    background-color: #f8f8f8;
                }
                .ql-container {
                    border: none !important;
                }
            </style>
        </head>
        <body>
            <div id="editor-container">
                <div id="editor">$innerText</div>
            </div>
            
            <script src="https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"></script>
            <script>
                const quill = new Quill('#editor', {
                    theme: 'snow',
                    modules: {
                        toolbar: [
                            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
                            ['bold', 'italic', 'underline'],
                            [{ 'color': [] }, { 'background': [] }],
                            [{ 'align': [] }],
                            ['clean']
                        ]
                    }
                });
                
                quill.on('text-change', function() {
                    const text = quill.root.innerHTML;
                    Android.onTextChange(text);
                });
            </script>
        </body>
        </html>
""".trimIndent()
