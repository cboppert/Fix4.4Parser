FIX4.4 New Order Single Message Parser
--------------------------------------

This program takes in a FIX4.4 message
and tells you if there are errors or no errors.

It looks for the required fields only. It ensures
checksums are last and are integers. It ensures
the body length is correct and is the second field.
It ensures the first field indicates the message
is of the type FIX4.4. It ensures the third field 
MsgType is third and finally it looks for the two
required fields which are always unencrypted.
Those are SenderCompID and TargetCompID.

* BeginString  (8= ) - Always First
* BodyLength   (9= ) - Always Second
* MsgType      (35=) - Always Third
* SenderCompID (49=) - Always Unencrypted
* TargetCompID (56=) - Always Unencrypted
* CheckSum     (10=) - Always Last
