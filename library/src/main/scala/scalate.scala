package net.physalis.unfiltered.scalate.servlet

import unfiltered.request._
import unfiltered.response._

import org.fusesource.scalate.{ Binding, RenderContext }
import org.fusesource.scalate.servlet.{ ServletRenderContext, ServletTemplateEngine }
import javax.servlet.{ Filter, FilterConfig, ServletContext }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

object Scalate {

  def apply(request: HttpRequest[HttpServletRequest],
            template: String,
            attributes: (String, Any)*)
  ( implicit
    engine: ServletTemplateEngine,
    servletContext: ServletContext,
    bindings: List[Binding] = Nil,
    additionalAttributes: Seq[(String, Any)] = Nil
  ) = new Responder[HttpServletResponse] {
    def respond(res: HttpResponse[HttpServletResponse]) {
      val printWriter = res.underlying.getWriter()
      try {
        val scalateTemplate = engine.load(template, bindings)
        val context = new ServletRenderContext(engine, printWriter, request.underlying, res.underlying, servletContext)
        (additionalAttributes ++ attributes) foreach {
          case (k,v) => context.attributes(k) = v
        }
        engine.layout(scalateTemplate, context)
      } catch {
        case e if engine.isDevelopmentMode =>
          printWriter.println("Exception: " + e.getMessage)
          e.getStackTrace.foreach(printWriter.println)
        case e => throw e
      }
    }
  }
}

trait DefaultScalateSupport extends Filter {
  implicit var engine: ServletTemplateEngine = _
  implicit var servletContext: ServletContext = _

  abstract override def init(config: FilterConfig) {
    super.init(config)
    servletContext = config.getServletContext
    engine = new ServletTemplateEngine(config)
    configureScalateTemplateEngine(engine)
  }

  def configureScalateTemplateEngine(engine: ServletTemplateEngine) {
    engine.layoutStrategy = org.fusesource.scalate.layout.NullLayoutStrategy
  }
}
