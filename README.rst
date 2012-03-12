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

sbt
~~~~~~~~~~~~~~~~~~~~~
::

    resolvers ++= Seq(
      "akr4 release" at "http://akr4.github.com/mvn-repo/releases"
    )

    "net.physalis" %% "unfiltered-scalate-servlet" % "2.0"

Plan
~~~~~~~~~~~~~~~~~~~~~
With default setting::

  import net.physalis.unfiltered.scalate.servlet.DefaultScalateSupport
  class Hello extends Plan with DefaultScalateSupport {
    def intent = {
      case req => Scalate(req, "WEB-INF/scalate/templates/hello.jade")
    }
  }

Customize TemplateEngine::

  trait ScalateSupport extends DefaultScalateSupport {
    override def configureScalateTemplateEngine(engine: ServletTemplateEngine) {
      engine.layoutStrategy = new DefaultLayoutStrategy(engine, "/scalate/layouts/default.jade")
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
::

  $ sbt
  sbt> project unfiltered-scalate-servlet-sample-web
  sbt> container:start
  
  $ curl -v "http://localhost:8080?name=akira"

License
---------
Copyright 2012 Akira Ueda

Licensed under the Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
