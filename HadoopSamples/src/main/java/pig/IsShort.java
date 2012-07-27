package pig;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.pig.FilterFunc;
import org.apache.pig.data.Tuple;

public class IsShort extends FilterFunc{
	private Logger log = Logger.getLogger(IsShort.class);
	
	@Override
	public Boolean exec(Tuple tuple) throws IOException {
		if ( tuple == null || tuple.isNull() || tuple.size() == 0 ){
			return false;
		}
		Object obj = tuple.get(0);
		log.info("processing tuple [" + obj + "]");
		if ( obj instanceof String){
			String st = (String)obj;
			if ( st.length() > 15 ){
				return false;
			}
			return true;
		}
		return false;
	}

}
