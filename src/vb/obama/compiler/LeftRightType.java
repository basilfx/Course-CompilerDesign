package vb.obama.compiler;

/**
 * Class which describes an left-right type checking.
 * @version 1.0
 */
public class LeftRightType {
	/**
	 * Left type
	 */
	private Class<?> left;
	
	/**
	 * Right type
	 */
	private Class<?> right;
	
	/**
	 * Construct a new left-rigth type
	 * @param left Left type
	 * @param right Right type
	 */
	public LeftRightType(Class<?> left, Class<?> right) {
		this.left = left;
		this.right = right;
	}
	
	/**
	 * Calculate hashcode of both types
	 * @return hascode
	 */
	@Override
	public int hashCode() {
		return this.left.hashCode() + this.right.hashCode();
	}
	
	/**
	 * Check if two types are the same. Two types are the same if
	 * (a, b) == (c, d) or (a, b) == (d, c)
	 * 
	 * @return true if this instance is the same as the other instance
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) return false;
		
		LeftRightType other = (LeftRightType) obj;
		
		if (this.left.equals(other.left) && this.right.equals(other.right)){
			return true;
		} if (this.right.equals(other.left) && this.left.equals(other.right)){
			return true;
		} else {
			return false;
		}
	}
}
