package util

import scienceworld.struct.EnvObject
import scienceworld.struct.EnvObject._

object StringHelpers {

  def objectListToStringDescription(objs:Set[EnvObject], mode:Int = MODE_CURSORY_DETAIL, multiline:Boolean = false):String = {
    val filteredObjs = objs.map(_.getDescriptionSafe(mode)).filter(_.isDefined).map(_.get)

    // No contents
    if (filteredObjs.size == 0) return "nothing"

    // Single line
    if (!multiline) return filteredObjs.mkString(", ")

    // Multi-line
    val os = new StringBuilder
    for (desc <- filteredObjs) {
      os.append("\t" + desc + "\n")
    }

    return os.toString
  }

}
