/**
 * 
 * @author paul
 *
 */


/*
 * IE: 
 * > - *
 *  
 * > group <groups>
 * > player <players>
 *  
 */
public class SensorTriggerCondition {
	public enum Type {
		TYPE,
		GROUP,
		PERMISSION,
		PLAYERNAME
	}
	
	public Type mType;
	public boolean mInverted;
	
	//public boolean IsFulfilled(LivingEntity)
	
}
