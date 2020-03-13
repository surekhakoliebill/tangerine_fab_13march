package com.docrecog.scan;

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;


public class RecogResult 
{
	public String lines = "";
	public String docType = "";
	public String country = "";
	public String surname = "";
	public String givenname = "";
	public String docnumber = "";
	public String docchecksum = "";
	public String nationality = "";
	public String birth = "";
	public String birthchecksum = "";
	public String sex = "";
	public String expirationdate = "";
	public String expirationchecksum = "";
	public String otherid = "";
	public String otheridchecksum = "";
	public String secondrowchecksum = "";
	public int ret = 0;
	public Bitmap faceBitmap = null;
	public Bitmap docBitmap = null; //byJJH20190607

	public boolean bocr;
	public float fConfidence = 0.0f;
	public String confidence = "";
	
	public void SetResult(int[] intData)
	{
		if (intData == null) //face
		{
			confidence = String.valueOf(fConfidence);
			return;
		}

		int i,k=0,len;
		byte[] tmp = new byte[100];
		try {
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			lines = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			docType = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			country = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			surname = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			givenname = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			docnumber = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			docchecksum = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			nationality = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			birth = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			birthchecksum = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			sex = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			expirationdate = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			expirationchecksum = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			otherid = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			otheridchecksum = convbyte2string(tmp);
			len = intData[k++];
			for (i = 0; i < len; ++i) tmp[i] = (byte) intData[k++];
			tmp[i] = 0;
			secondrowchecksum = convbyte2string(tmp);
		}catch(UnsupportedEncodingException e){

		}

	}
	public String  GetResultString()
	{
		String str;

		if (bocr == false)
		{
			str = "value = 1";
			return str;
		}

		if(ret == 1)
		{
			str = "correct doc\n";
		}
		else {
			str = "incorrect doc\n";
		}
		/*str = str + lines + "\n"
			 + docType + "\n"
			 + country + "\n"
			 + surname + "\n"
			 + givenname + "\n"
			 + docnumber + "\n"
			 + docchecksum + "\n"
			 + nationality + "\n"
			 + birth + "\n"
			 + birthchecksum + "\n"
			 + sex + "\n"
			 + expirationdate + "\n"
			 + expirationchecksum + "\n"
			 + otherid + "\n"
			 + otheridchecksum + "\n"
			 + secondrowchecksum + "\n";*/

		str += "Lines : " + lines + "\n"
			+ "Document Type : " + docType + "\n"
		 	+ "Country : " + country + "\n"
			+ "Surname : " + surname + "\n"
			+ "Given Names : " + givenname + "\n"
			+ "Document No. : " + docnumber + "\n"
			+ "Document Check Number: " + docchecksum + "\n"
			+ "Nationaltiy : " + nationality + "\n"
			+ "Birth Date : " + birth + "\n"
			+ "Birth Check Number: " + birthchecksum + "\n"
			+ "Sex : " + sex + "\n"
			+ "ExpirationDate : " + expirationdate + "\n"
			+ "Expiration Check Number: " + expirationchecksum + "\n";

		//if (otherid.length() > 0) {
			str += "Other ID : " + otherid + "\n"
				+ "Other ID Check: " + otheridchecksum + "\n";
		//}

		str += "Flag : " + Integer.toString(ret) + "\n";

		if (faceBitmap != null) str = "value = 0 \n" + str;
		else str = "value = 2 \n" + str;

		return str;
	}
	public static int getByteLength(byte[] bystr, int maxLen) {
		int i, len = 0;
		for (i = 0; i < maxLen; ++i)
		{
			if (bystr[i] == 0)
				break;
		}
		len = i;
		return len;
	}

	public static String convbyte2string(byte[] bystr) throws UnsupportedEncodingException {
		int len = getByteLength(bystr, 2000);
		String outStr = new String(bystr, 0, len ,"UTF-8");
		return outStr;
	}
}
