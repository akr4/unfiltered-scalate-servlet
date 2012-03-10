package hello

import net.physalis.unfiltered.scalate.servlet.Scalate
import javax.servlet.{ FilterConfig, ServletContext }
import unfiltered.filter.Plan
import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.servlet.{ ServletTemplateEngine, ServletRenderContext }

object MyFunction {
  import org.fusesource.scalate.servlet.ServletRenderContext._
  import org.fusesource.scalate.RenderContext.capture

  def greeting = "Hello " + request.getParameter("name")

  def articleLink(id: Int)(body: => Unit) =
    <a href={ contextPath + "/article/" + id }>{ capture(body) }</a>

  private def contextPath = request.getContextPath
}

class Hello extends Plan {
  implicit var engine: TemplateEngine = _
  implicit var servletContext: ServletContext = _

  override def init(config: FilterConfig) {
    engine = new ServletTemplateEngine(config)
    engine.mode = "dev" // or production
    servletContext = config.getServletContext
  }

  def intent = {
    case req => Scalate(req, "WEB-INF/scalate/templates/hello.jade")
  }
}
