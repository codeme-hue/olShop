package id.kardihaekal.olshop.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Common {
  public static String convertToRupiah(String value){
    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();
    formatRp.setCurrencySymbol("Rp. ");
    formatRp.setMonetaryDecimalSeparator(',');
    formatRp.setGroupingSeparator('.');
    kursIndonesia.setDecimalFormatSymbols(formatRp);
    return String.valueOf(kursIndonesia.format(Double.parseDouble(value)));
  }

}
