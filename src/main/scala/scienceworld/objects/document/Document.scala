package scienceworld.objects.document

import scienceworld.properties.{GlassProp, IsActivableDeviceOff, IsNotContainer, IsUsable, MoveableProperties, PaperProp}
import scienceworld.struct.EnvObject

class Document extends EnvObject {
  this.name = "document"
  var title = "document"
  var contents = "This is a sample document."

  this.propMaterial = Some(new PaperProp())
  this.propContainer = Some( new IsNotContainer() )
  this.propDevice = None
  this.propMoveable = Some(new MoveableProperties(isMovable = true))

  def readDocument():String = {
    this.title + "\n" + this.contents
  }

  override def getReferents(): Set[String] = {
    Set(this.name, this.title, this.name + " called " + this.title)
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("A document titled " + this.title)

    os.toString
  }

}


class Paper extends Document {
  this.name = "paper"

  override def readDocument():String = {
    val os = new StringBuilder()

    os.append("The paper reads:\n")
    os.append(this.title + "\n")
    os.append(this.contents)

    os.toString()
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("A paper titled " + this.title)

    os.toString
  }


}

class Book extends Document {
  this.title = "title"
  var author = "author"
  this.contents = "contents"

  this.name = title + " by " + author

  override def readDocument():String = {
    val os = new StringBuilder()

    os.append("The book reads:\n")
    os.append(this.title + " by " + this.author + "\n")
    os.append(this.contents)

    os.toString()
  }

  override def getReferents(): Set[String] = {
    Set("book", this.title, "book by " + this.author, this.title + " book", this.name)
  }

  override def getDescription(): String = {
    val os = new StringBuilder

    os.append("A book titled " + this.title + " by " + this.author)

    os.toString

  }

}

class BookMobyDick extends Book {
  this.title = "Moby Dick"
  this.author = "Herman Melville"
  this.contents = "Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people’s hats off—then, I account it high time to get to sea as soon as I can. Call me Ishmael. Some years ago—never mind how long precisely—having little or no money in my purse, and nothing particular to interest me on shore, I thought I would sail about a little and see the watery part of the world. It is a way I have of driving off the spleen and regulating the circulation. Whenever I find myself growing grim about the mouth; whenever it is a damp, drizzly November in my soul; whenever I find myself involuntarily pausing before coffin warehouses, and bringing up the rear of every funeral I meet; and especially whenever my hypos get such an upper hand of me, that it requires a strong moral principle to prevent me from deliberately stepping into the street, and methodically knocking people’s hats off—then, I account it high time to get to sea as soon as I can. "

  this.name = "book (" + this.title + ")"
}

class BookFrankenstein extends Book {
  this.title = "Frankenstein"
  this.author = "Mary Wollstonecraft Shelley"
  this.contents = "You will rejoice to hear that no disaster has accompanied the commencement of an enterprise which you have regarded with such evil forebodings. I arrived here yesterday, and my first task is to assure my dear sister of my welfare and increasing confidence in the success of my undertaking.\n\nI am already far north of London, and as I walk in the streets of Petersburgh, I feel a cold northern breeze play upon my cheeks, which braces my nerves and fills me with delight. Do you understand this feeling? This breeze, which has travelled from the regions towards which I am advancing, gives me a foretaste of those icy climes. "

  this.name = "book (" + this.title + ")"
}