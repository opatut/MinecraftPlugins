/**
 * 
 * @author paul
 * 
 */
public class SensorTriggerCondition {
	public enum ConditionType {
		TYPE, GROUP, COMMAND, PLAYER, ANY, INVALID
	}

	public enum ConditionModifier {
		NEEDED, FORBIDDEN, NORMAL
	}

	public enum ScanResult {
		/** If forbidden was true or needed was false. */
		RETURN_FALSE,

		/** If normal was false or the condition was invalid. */
		CONTINUE_FALSE,

		/** If normal was true, forbidden was false or needed was true */
		CONTINUE_TRUE,
	}

	public ConditionType mType = ConditionType.ANY;
	public String mValue = "";
	public ConditionModifier mModifier = ConditionModifier.NORMAL;

	public SensorTriggerCondition(String condition) {
		condition = condition.trim();
		String[] parts = condition.split(":", 2);

		if (parts.length == 0) {
			mType = ConditionType.ANY;
		} else {
			if (parts[0].startsWith("+")) {
				mModifier = ConditionModifier.NEEDED;
			} else if (parts[0].startsWith("-")) {
				mModifier = ConditionModifier.FORBIDDEN;
			} else {
				parts[0] = " " + parts[0];
			}

			if (parts[0].length() < 2) {
				mType = ConditionType.INVALID;
			} else {
				char t = parts[0].charAt(1);
				if (t == '*') {
					mType = ConditionType.ANY;
				} else if (t == 'c') {
					mType = ConditionType.COMMAND;
				} else if (t == 'g') {
					mType = ConditionType.GROUP;
				} else if (t == 'p') {
					mType = ConditionType.PLAYER;
				} else if (t == 't') {
					mType = ConditionType.TYPE;
				} else {
					mType = ConditionType.INVALID;
				}
			}

			if (parts.length == 2) {
				mValue = parts[1];
			}
		}
	}

	public ScanResult ScanFor(LivingEntity e) {
		return GetScanResult(LivingEntityFits(e));
	}

	private boolean LivingEntityFits(LivingEntity e) {
		if (mType == ConditionType.ANY)
			return true;
		else if (mType == ConditionType.TYPE) {
			if (mValue.equalsIgnoreCase("animal")) {
				return e.isAnimal();
			} else if (mValue.equalsIgnoreCase("mob")) {
				return e.isMob() && !e.isPlayer();
			} else if (mValue.equalsIgnoreCase("player")) {
				return e.isPlayer();
			} else {
				return false;
			}
		} else if (mType == ConditionType.PLAYER) {
			if (!e.isPlayer())
				return false;
			return e.getPlayer().getName().equals(mValue);
		} else if (mType == ConditionType.COMMAND) {
			if (!e.isPlayer())
				return false;
			return e.getPlayer().canUseCommand(mValue);
		} else if (mType == ConditionType.GROUP) {
			if (!e.isPlayer())
				return false;
			return e.getPlayer().isInGroup(mValue);
		}
		// the type is invalid !?
		return false;
	}

	private ScanResult GetScanResult(boolean matches) {
		if (mModifier == ConditionModifier.NEEDED) {
			if (matches)
				return ScanResult.CONTINUE_TRUE;
			else
				return ScanResult.RETURN_FALSE;
		} else if (mModifier == ConditionModifier.FORBIDDEN) {
			if (matches)
				return ScanResult.RETURN_FALSE;
			else
				return ScanResult.CONTINUE_TRUE;
		} else if (mModifier == ConditionModifier.NORMAL) {
			if (matches)
				return ScanResult.CONTINUE_TRUE;
			else
				return ScanResult.CONTINUE_FALSE;
		} else {
			return ScanResult.CONTINUE_FALSE;
		}
	}

}
