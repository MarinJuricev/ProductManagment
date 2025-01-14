import SwiftUI
import WebKit

public struct QuillEditorWebView: UIViewRepresentable {
    @Binding private var text: String
    private let webView = RichEditorWebView()

    public init(text: Binding<String>) {
        self._text = text
    }

    public func makeUIView(context: Context) -> some WKWebView {
        settingWebView(context: context)

        webView.didReceive = { message in
            processMessage(message: message)
        }

        loadEditor()

        return webView
    }

    public func updateUIView(_ uiView: UIViewType, context: Context) { }

    private func processMessage(message: WKScriptMessage) {
        guard message.name == "textDidChange" else {
            return
        }

        let changeText = (message.body as? String) ?? ""
        text = changeText
    }

    private func loadEditor() {
        DispatchQueue.main.async {
            webView.loadHTMLString(generateHTML(), baseURL: Bundle.main.bundleURL)
        }
    }

    private func settingWebView(context: Context) {
        webView.scrollView.bounces = false
        webView.scrollView.isScrollEnabled = false

        webView.isOpaque = false
        webView.backgroundColor = UIColor.clear
        webView.scrollView.backgroundColor = UIColor.clear
    }
}

extension QuillEditorWebView {
    func generateHTML() -> String {
        return """
              <HTML>
                  <head>
                      <meta name='viewport' content='width=device-width, shrink-to-fit=YES, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no'>
                  </head>
                  <!-- Include stylesheet -->
                  <link href="https://cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">

                    <style>
                      .ql-font-roboto {
                        font-family: 'Roboto', sans-serif;
                      }
                      #editor { height: 600px; }
                      body {background-color: #E2E6EB;}
                    </style>

                  <!-- Create the editor container -->
                  <BODY>
                      <div id="editor">\(text)</div>
                  </BODY>
                  <!-- Include the Quill library -->
                                    <script src="https://cdn.quilljs.com/1.3.6/quill.js"></script>

                  <!-- Initialize Quill editor -->
                  \(generateJS())
              </HTML>

              """
    }

    // swiftlint:disable:next function_body_length
    func generateJS() -> String {
        return """
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

        function debounce(func, delay) {
             let timeoutId;
             return function() {
                 const context = this;
                 const args = arguments;
                 clearTimeout(timeoutId);
                 timeoutId = setTimeout(function() {
                     func.apply(context, args);
                 }, delay);
             };
         }

         function setQuillContent(htmlContent) {
             quill.clipboard.dangerouslyPasteHTML(htmlContent);
         }

         function throttle(func, limit) {
             let lastFunc;
             let lastRan;
             return function() {
                 const context = this;
                 const args = arguments;
                 if (!lastRan) {
                     func.apply(context, args);
                     lastRan = Date.now();
                 } else {
                     clearTimeout(lastFunc);
                     lastFunc = setTimeout(function() {
                         if ((Date.now() - lastRan) >= limit) {
                             func.apply(context, args);
                             lastRan = Date.now();
                         }
                     }, limit - (Date.now() - lastRan));
                 }
             };
         }

         const debouncedTextDidChange = debounce(function(content) {
             window.webkit.messageHandlers.textDidChange.postMessage(content);
         }, 1000);


          quill.on('text-change', function(delta, oldDelta, source) {
               const content = quill.root.innerHTML;
               debouncedTextDidChange(content);
          });

      </script>

 """
    }
}
