package hello

import net.physalis.unfiltered.scalate.servlet.DefaultScalateSupport
import unfiltered.filter.Plan
import unfiltered.scalate.Scalate
import org.fusesource.scalate.servlet.ServletTemplateEngine

object MyFunction {
  import org.fusesource.scalate.servlet.ServletRenderContext._
  import org.fusesource.scalate.RenderContext.capture

  def greeting = "Hello " + request.getParameter("name")

  def articleLink(id: Int)(body: => Unit) =
    <a href={ contextPath + "/article/" + id }>{ capture(body) }</a>

  private def contextPath = request.getContextPath
}

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
