/*
 * Copyright 2012 Akira Ueda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

trait ExceptionHandler extends javax.servlet.Filter {
  import javax.servlet._
  abstract override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
    try {
      super.doFilter(request, response, chain)
    } catch {
      case e => e.printStackTrace; throw e
    }
  }
}

class Hello extends Plan with ScalateSupport with ExceptionHandler {
  def intent = {
    case req => Scalate(req, "WEB-INF/scalate/templates/hello.jade")
  }
}
