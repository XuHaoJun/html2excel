package com.github.xuhaojun

import org.apache.poi.xssf.usermodel._
import org.apache.poi.ss.usermodel.{Font, FontUnderline}

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.{Document, Element, ElementNode, Node, TextNode}

import scala.collection.mutable.ListBuffer

import java.io.FileOutputStream


object Main {
  def main(args: Array[String]): Unit = {
    val browser = JsoupBrowser()
    val doc = browser.parseString("<strong><u>fff</u><br/><i>LLLL</i><strong>")

    val wb = new XSSFWorkbook()
    val createHelper = wb.getCreationHelper
    val sheet = wb.createSheet("new sheet")
    val row = sheet.createRow(0)

    val xs = traverse(doc)

    var text = ""
    for (x <- xs) {
      text += x.content
    }

    val richText = createHelper.createRichTextString(text)
    var end = 0
    for (x <- xs) {
      val start = end
      end += x.content.length

      val font = wb.createFont()
      font.setBold(x.style.font.getBold)
      font.setItalic(x.style.font.getItalic)
      font.setUnderline(x.style.font.getUnderline)
      richText.applyFont(start, end, font)
    }


    row.createCell(0).setCellValue(richText)
    val fileOut = new FileOutputStream("/home/xuhaojun/Desktop/foo.xlsx")
    wb.write(fileOut)
    fileOut.close()

    println(text)
  }

  def traverse(doc: Document): ListBuffer[RichTextValue] = {
    val output: ListBuffer[RichTextValue] = ListBuffer()
    dfsLeftLoop(doc.body.childNodes, new RichTextStyle(new XSSFFont()), output)
    output
  }

  def dfsLeftLoop(childNodes: Iterable[Node], env0: RichTextStyle, output: ListBuffer[RichTextValue]): Unit = {
    childNodes foreach {
      case ElementNode(element) =>
        val env1 = parseTag(element, env0, output)
        dfsLeftLoop(element.childNodes, env1, output)

      case TextNode(content) =>
        output += new RichTextValue(env0, content)
    }
  }

  def parseTag(tag: Element, env0: RichTextStyle, output: ListBuffer[RichTextValue]): RichTextStyle = {
    tag.tagName match {
      case "br" =>
        output += new RichTextValue(new RichTextStyle(new XSSFFont()), "\n")
        env0

      case "strong" | "b" =>
        val font = new XSSFFont()
        font.setBold(true)
        val env1: RichTextStyle = env0.cloneByFont(font)
        env1

      case "em" | "i" =>
        val font = new XSSFFont()
        font.setItalic(true)
        val env1: RichTextStyle = env0.cloneByFont(font)
        env1

      case "ins" | "u" =>
        val font = new XSSFFont()
        font.setUnderline(FontUnderline.SINGLE)
        val env1: RichTextStyle = env0.cloneByFont(font)
        env1

      case _ =>
        env0
    }
  }
}

class RichTextStyle(var font: XSSFFont) {
  def cloneByFont(ext: XSSFFont): RichTextStyle = {
    val nextFont = new XSSFFont()
    nextFont.setBold(this.font.getBold)
    nextFont.setItalic(this.font.getItalic)
    nextFont.setUnderline(this.font.getUnderline)

    if (ext.getBold) {
      nextFont.setBold(ext.getBold)
    }
    if (ext.getItalic) {
      nextFont.setItalic(ext.getItalic)
    }
    if (ext.getUnderline != FontUnderline.NONE.getByteValue) {
      nextFont.setUnderline(ext.getUnderline)
    }

    new RichTextStyle(nextFont)
  }
}

class RichTextValue(var style: RichTextStyle, var content: String) {
}
