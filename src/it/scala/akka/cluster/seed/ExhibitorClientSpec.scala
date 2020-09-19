package akka.cluster.seed

import language.postfixOps
import akka.actor.ActorSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers._
import org.scalatest.wordspec._
import com.typesafe.config.ConfigFactory

import concurrent.duration._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers

import scala.concurrent.Await

class ExhibitorClientSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll with ScalaFutures {


  override implicit def patienceConfig: PatienceConfig = PatienceConfig(timeout = 5 seconds)

  var system:ActorSystem = _


  "ExhibitorClient" must {
    "read the zookeepers from exhibitor" in {
      val zks = ExhibitorClient(system, sys.env("EXHIBITOR_URL"), "/exhibitor/v1/cluster/list", false).getZookeepers(Some("chroot")).futureValue
      zks must endWith("/chroot")
      zks must not startWith("/chroot")
    }
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    Await.result(system.terminate(), Duration.Inf)
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    system = ActorSystem("test", ConfigFactory.empty())
  }
}
