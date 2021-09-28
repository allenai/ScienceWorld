package scienceworld.Objects.agent

import scienceworld.struct.EnvObject



class Agent extends EnvObject {
  this.name = "agent"

  override def getReferents(): Set[String] = {
    Set("agent", "self")
  }

  override def getDescription(): String = {
    return ("the agent")
  }

}
