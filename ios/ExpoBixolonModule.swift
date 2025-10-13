import ExpoModulesCore

public class ExpoBixolonModule: Module {
  public func definition() -> ModuleDefinition {
    Name("ExpoBixolon")

    Constants([
      "PI": Double.pi
    ])

    Events("onChange")

    Function("hello") {
      return "Hello world! 👋"
    }

    AsyncFunction("setValueAsync") { (value: String) in
      self.sendEvent("onChange", [
        "value": value
      ])
    }

    View(ExpoBixolonView.self) {
      Prop("url") { (view: ExpoBixolonView, url: URL) in
        if view.webView.url != url {
          view.webView.load(URLRequest(url: url))
        }
      }

      Events("onLoad")
    }
  }
}
