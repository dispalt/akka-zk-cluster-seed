package akka.cluster.seed.zookeeper

import akka.remote.testkit.MultiNodeSpecCallbacks
import org.scalatest._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.language.postfixOps

trait ScalaTestMultiNodeSpec extends MultiNodeSpecCallbacks with AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

}
