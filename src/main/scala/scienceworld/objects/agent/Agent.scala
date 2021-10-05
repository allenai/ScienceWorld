package scienceworld.objects.agent

import scienceworld.struct.EnvObject



class Agent extends EnvObject {
  this.name = "agent"

  override def getReferents(): Set[String] = {
    Set("agent", "self")
  }

  override def getDescription(mode:Int): String = {
    return ("the agent")
  }

}
