/**
 * 
 * @author opatut
 *
 */
public abstract class CustomBlock {	
	public abstract String serialize();
	public abstract CustomBlock deserialize(String string);
}
