/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.11.30 at 08:42:57 AM GMT 
//

package com.pega.gcs.logviewer.model.measure;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Log4jMeasurePattern" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="PatternString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "log4JMeasurePattern" })
@XmlRootElement(name = "Log4jMeasurePatternList")
public class Log4JMeasurePatternList {

	@XmlElement(name = "Log4jMeasurePattern", required = true)
	protected List<Log4JMeasurePatternList.Log4JMeasurePattern> log4JMeasurePattern;

	/**
	 * Gets the value of the log4JMeasurePattern property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the log4JMeasurePattern property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLog4JMeasurePattern().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link Log4JMeasurePatternList.Log4JMeasurePattern }
	 * 
	 * 
	 */
	public List<Log4JMeasurePatternList.Log4JMeasurePattern> getLog4JMeasurePattern() {
		if (log4JMeasurePattern == null) {
			log4JMeasurePattern = new ArrayList<Log4JMeasurePatternList.Log4JMeasurePattern>();
		}
		return this.log4JMeasurePattern;
	}

	/**
	 * <p>
	 * Java class for anonymous complex type.
	 * 
	 * <p>
	 * The following schema fragment specifies the expected content contained
	 * within this class.
	 * 
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="PatternString" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *         &lt;element name="Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *         &lt;element name="Color" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *       &lt;/sequence>
	 *     &lt;/restriction>
	 *   &lt;/complexContent>
	 * &lt;/complexType>
	 * </pre>
	 * 
	 * 
	 */
	@XmlAccessorType(XmlAccessType.FIELD)
	@XmlType(name = "", propOrder = { "name", "patternString", "unit", "color" })
	public static class Log4JMeasurePattern {

		@XmlElement(name = "Name", required = true)
		protected String name;
		@XmlElement(name = "PatternString", required = true)
		protected String patternString;
		@XmlElement(name = "Unit")
		protected String unit;
		@XmlElement(name = "Color")
		protected String color;

		/**
		 * Gets the value of the name property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getName() {
			return name;
		}

		/**
		 * Sets the value of the name property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setName(String value) {
			this.name = value;
		}

		/**
		 * Gets the value of the patternString property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getPatternString() {
			return patternString;
		}

		/**
		 * Sets the value of the patternString property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setPatternString(String value) {
			this.patternString = value;
		}

		/**
		 * Gets the value of the unit property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getUnit() {
			return unit;
		}

		/**
		 * Sets the value of the unit property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setUnit(String value) {
			this.unit = value;
		}

		/**
		 * Gets the value of the color property.
		 * 
		 * @return possible object is {@link String }
		 * 
		 */
		public String getColor() {
			return color;
		}

		/**
		 * Sets the value of the color property.
		 * 
		 * @param value
		 *            allowed object is {@link String }
		 * 
		 */
		public void setColor(String value) {
			this.color = value;
		}

	}

}
