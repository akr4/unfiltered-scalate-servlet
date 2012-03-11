unfiltered-scalate-servlet
================================
unfiltered-scalate-servlet is a library to utilize Scalate with Unfiltered in servlet environment.

Unlike the official unfiltered-scalate, this library uses ServletRenderContext, so you can use servlet specific features in your view support functions as follows::

  object MyFunction {
    import org.fusesource.scalate.servlet.ServletRenderContext._
    def greeting = "Hello " + request.getParameter("name")
  }

How to use
-----------------------
With default setting::

  import net.physalis.unfiltered.scalate.servlet.{ Scalate, DefaultScalateSupport }
  class Hello extends Plan with DefaultScalateSupport {
    def intent = {
      case req => Scalate(req, "WEB-INF/scalate/templates/hello.jade")
    }
  }

Customize TemplateEngine::

  trait ScalateSupport extends DefaultScalateSupport {
    override def configureScalateTemplateEngine(engine: ServletTemplateEngine) {
      engine.layoutStrategy = org.fusesource.scalate.layout.NullLayoutStrategy
      engine.mode = "dev" // or production
    }
  }
  
  class Hello extends Plan with ScalateSupport {
    def intent = {
      case req => Scalate(req, "WEB-INF/scalate/templates/hello.jade")
    }
  }


Sample Application
-------------------------
  $ sbt
  sbt> project unfiltered-scalate-servlet-web
  sbt> container:start
  
  $ curl -v "http://localhost:8080?name=akira"

