package net.physalis.unfiltered.scalate.servlet

import unfiltered.request._
import unfiltered.response._

import org.fusesource.scalate.{TemplateEngine, Binding, RenderContext}
import org.fusesource.scalate.servlet.ServletRenderContext
import javax.servlet.ServletContext
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

case class Scalate(request: HttpRequest[HttpServletRequest], template: String, attributes:(String,Any)*)
  (
    implicit engine: TemplateEngine,
    servletContext: ServletContext,
    bindings: List[Binding] = List[Binding]()
  ) extends Responder[HttpServletResponse] {
  
  def respond(res: HttpResponse[HttpServletResponse]) {
    val writer = res.underlying.getWriter()
    try {
      val context = new ServletRenderContext(engine, request.underlying, res.underlying, servletContext)
      for ((k, v) <- attributes) context.attributes(k) = v
      engine.layoutStrategy = org.fusesource.scalate.layout.NullLayoutStrategy
      engine.layout(template, context)
    }
    catch { case e => e.printStackTrace; throw e }
    finally { writer.close() }
  }
}
