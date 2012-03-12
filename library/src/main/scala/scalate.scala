package net.physalis.unfiltered.scalate.servlet

import unfiltered.request._
import unfiltered.response._

import org.fusesource.scalate.{ Binding, RenderContext, TemplateEngine }
import org.fusesource.scalate.servlet.{ ServletRenderContext, ServletTemplateEngine }
import java.io.PrintWriter
import javax.servlet.{ Filter, FilterChain, FilterConfig, ServletContext, ServletRequest, ServletResponse }
import javax.servlet.http.{ HttpServletRequest, HttpServletResponse }

trait DefaultScalateSupport extends Filter {
  import util.DynamicVariable

  implicit var engine: ServletTemplateEngine = _
  implicit var servletContext: ServletContext = _
  implicit def renderContext(path: String, writer: PrintWriter, engine: TemplateEngine): RenderContext =
    new ServletRenderContext(
      engine, writer, httpServletRequest.value, httpServletResponse.value, servletContext
    )

  val httpServletRequest = new DynamicVariable[HttpServletRequest](null)
  val httpServletResponse = new DynamicVariable[HttpServletResponse](null)

  abstract override def init(config: FilterConfig) {
    super.init(config)
    servletContext = config.getServletContext
    engine = new ServletTemplateEngine(config)
    configureScalateTemplateEngine(engine)
  }

  abstract override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    httpServletRequest.withValue(request.asInstanceOf[HttpServletRequest]) {
      httpServletResponse.withValue(response.asInstanceOf[HttpServletResponse]) {
	super.doFilter(request, response, chain)
      }
    }
  }

  def configureScalateTemplateEngine(engine: ServletTemplateEngine) {
    engine.layoutStrategy = org.fusesource.scalate.layout.NullLayoutStrategy
  }
}
