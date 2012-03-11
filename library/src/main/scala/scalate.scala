package net.physalis.unfiltered.scalate.servlet

import unfiltered.request._
import unfiltered.response._

import org.fusesource.scalate.{ Binding, RenderContext }
import org.fusesource.scalate.servlet.{ ServletRenderContext, ServletTemplateEngine }
import javax.servlet.{ Filter, FilterConfig, ServletContext }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

case class Scalate(
  request: HttpRequest[HttpServletRequest],
  template: String,
  attributes: (String, Any)*
  )
  (
    implicit engine: ServletTemplateEngine,
    servletContext: ServletContext,
    bindings: List[Binding] = List[Binding]()
  ) extends Responder[HttpServletResponse] {
  
  def respond(res: HttpResponse[HttpServletResponse]) {
    val writer = res.underlying.getWriter()
    try {
      val context = new ServletRenderContext(engine, request.underlying, res.underlying, servletContext)
      for ((k, v) <- attributes) context.attributes(k) = v
      engine.layout(template, context)
    }
    catch { case e => e.printStackTrace; throw e }
    finally { writer.close() }
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
