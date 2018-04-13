package sla.negotiation.model;

import java.io.Serializable;
import java.util.Objects;

public class QOS implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
    private double[] range;
    private double value;
    private boolean positive;
    private double weight;

    public QOS() {
  
    }
    
	/**
     * 
     * @param name
     * @param value
     * @param positive
     * @param weight
     */
    public QOS(String name, double value, boolean positive, double weight) {
    	this.name = name;
    	this.value = value;
    	this.positive = positive;
    	this.weight = weight;
    }
    
   
    
    /**
     * 
     * @param name
     * @param range
     * @param positive
     * @param weight
     */
   /* public QOS(String name, double[] range, boolean positive, double weight) {
    	this.name = name;
    	this.range = range;
    	this.positive = positive;
    	this.weight = weight;
    }*/
     
    public QOS clone(){
    	QOS clone = new QOS(this.name, this.value, this.positive, this.weight);
		return clone;
    	
    }

    public void setName(String name) {
		this.name = name;
	}

	public void setValue(double value) {
		this.value = value;
	}
   
    public double[] getRange() {
		return range;
	}


	public void setRange(double[] range) {
		this.range = range;
	}


	public double getWeight() {
		return weight;
	}


	public void setWeight(double weight) {
		this.weight = weight;
	}


	public double getValue() {
        return value;
    }
    
    public String getName() {
        return name;
    }
    
    
    
    public boolean getPositive() {
		return positive;
	}


	public void setPositive(boolean positive) {
		this.positive = positive;
	}


	public int hashCode() {
        return Objects.hash(name);
    }

    
	public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QOS other = (QOS) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
	}
}
