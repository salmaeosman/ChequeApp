package services;

class NombreArabe {

    private static final String[] units = {
        "", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة",
        "ستة", "سبعة", "ثمانية", "تسعة", "عشرة", "أحد عشر", "اثنا عشر",
        "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر",
        "ثمانية عشر", "تسعة عشر"
    };

    private static final String[] tens = {
        "", "", "عشرون", "ثلاثون", "أربعون", "خمسون",
        "ستون", "سبعون", "ثمانون", "تسعون"
    };

    private static final String[] scales = {
        "", "ألف", "مليون", "مليار", "ترليون"
    };

    private static final String[] hundredsUnits = {
        "", "", "", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثمان", "تسع"
    };

    public static String convert(double montant) {
        if (montant == 0) return "صفر درهم";

        long dirhams = (long) montant;
        long centimes = Math.round((montant - dirhams) * 100);

        StringBuilder result = new StringBuilder();

        if (dirhams > 0) {
            if (dirhams == 1) {
                result.append("درهم واحد");
            } else if (dirhams == 2) {
                result.append("درهمان");
            } else {
                String numberWords = convertNumber(dirhams);
                result.append(numberWords);
                if (!numberWords.isEmpty()) {
                    result.append(" ");
                }
                result.append(getCurrencyWord(dirhams));
            }
        }

        if (centimes > 0) {
            if (dirhams > 0) {
                result.append(" و");
            }

            if (centimes == 1) {
                result.append("سنتيم واحد");
            } else if (centimes == 2) {
                result.append("سنتيمان");
            } else {
                String centimeWords = convertNumber(centimes);
                result.append(centimeWords);
                if (!centimeWords.isEmpty()) {
                    result.append(" ");
                }
                result.append(getCentimeWord(centimes));
            }
        }

        return result.toString();
    }

    private static String convertNumber(long number) {
        if (number == 0) return "";

        String[] parts = new String[scales.length];
        int scaleIndex = 0;
        long temp = number;

        while (temp > 0 && scaleIndex < scales.length) {
            int segment = (int) (temp % 1000);
            if (segment > 0) {
                String segmentText = convertLessThan1000(segment);

                if (segment == 2 && scaleIndex >= 0) {
                    segmentText = "";
                } else if (segment == 1 && scaleIndex > 0) {
                    segmentText = "";
                }

                if (scaleIndex > 0) {
                    String scaleName = getScaleName(segment, scaleIndex);
                    if (segmentText.isEmpty()) {
                        segmentText = scaleName;
                    } else {
                        segmentText += " " + scaleName;
                    }
                }

                parts[scaleIndex] = segmentText;
            }
            temp /= 1000;
            scaleIndex++;
        }

        StringBuilder result = new StringBuilder();
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i] != null && !parts[i].isEmpty()) {
                if (result.length() > 0) {
                    result.append(" و");
                }
                result.append(parts[i]);
            }
        }

        return result.toString();
    }

    private static String convertLessThan1000(int number) {
        if (number == 0) return "";
        if (number < 20) return units[number];

        if (number < 100) {
            int unit = number % 10;
            int ten = number / 10;
            return unit == 0 ? tens[ten] : units[unit] + " و" + tens[ten];
        }

        int rem = number % 100;
        int hundred = number / 100;

        String hundredStr;
        if (hundred == 1) {
            hundredStr = "مائة";
        } else if (hundred == 2) {
            hundredStr = (rem == 0) ? "مئتا" : "مئتان";
        } else {
            hundredStr = hundredsUnits[hundred] + "مائة";
        }

        return rem > 0 ? hundredStr + " و" + convertLessThan1000(rem) : hundredStr;
    }

    private static String getScaleName(int number, int scaleIndex) {
        if (scaleIndex == 1) {
            if (number == 1) return "ألف";
            if (number == 2) return "ألفان";
            if (number >= 3 && number <= 10) return "آلاف";
            return "ألفاً";
        }
        if (scaleIndex == 2) {
            if (number == 1) return "مليون";
            if (number == 2) return "مليونان";
            if (number >= 3 && number <= 10) return "ملايين";
            return "مليونًا";
        }
        if (scaleIndex == 3) {
            if (number == 1) return "مليار";
            if (number == 2) return "ملياران";
            if (number >= 3 && number <= 10) return "مليارات";
            return "مليارًا";
        }
        if (scaleIndex == 4) {
            if (number == 1) return "ترليون";
            if (number == 2) return "ترليونان";
            if (number >= 3 && number <= 10) return "ترليونات";
            return "ترليونًا";
        }
        return "";
    }

    private static String getCurrencyWord(long number) {
        long lastTwo = number % 100;
        if (lastTwo == 1) return "درهم";
        if (lastTwo == 2) return "دراهم";
        if (lastTwo >= 3 && lastTwo <= 10) return "دراهم";
        return "درهمًا";
    }

    private static String getCentimeWord(long number) {
        if (number >= 3 && number <= 10) {
            return "سنتيمات";
        } else {
            return "سنتيمًا";
        }
    }
}