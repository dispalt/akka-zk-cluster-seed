package akka.cluster.seed

import akka.actor.ActorSystem
import akka.cluster.ZookeeperClusterSeedSettings
import akka.testkit.TestKit
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.wordspec.AnyWordSpecLike
import org.scalatest.matchers.should.Matchers

class ZookeeperClusterSeedSettingsSpec extends TestKit(ActorSystem("test", ZookeeperClusterSeedSettingsSpec.config))
    with AnyWordSpecLike with Matchers {

  "ZookeeperClusterSeedSettings " should {
    "parse authentication options from config" in {
      val settings = new ZookeeperClusterSeedSettings(system)
      settings.ZKAuthorization should be(Some(("digest", "foo:bar")))
    }
  }
}

object ZookeeperClusterSeedSettingsSpec {
  val config: Config = ConfigFactory.parseString("""
         akka.cluster.seed.zookeeper {
             authorization {
               scheme = "digest"
               auth = "foo:bar"
             }
         }
       """)
}
