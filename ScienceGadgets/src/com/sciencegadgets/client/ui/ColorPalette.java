package com.sciencegadgets.client.ui;

import com.google.gwt.dom.client.StyleInjector;

public class ColorPalette {

	public static void SET_PALETTE(String mainColor) {
		if (mainColor == null || mainColor.length() != 6) {
			mainColor = "000000";
		}

		try {
			int r = Integer.valueOf(mainColor.substring(0, 2), 16);
			int g = Integer.valueOf(mainColor.substring(2, 4), 16);
			int b = Integer.valueOf(mainColor.substring(4, 6), 16);

			if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {

				double brightness = (0.299*r + 0.587*g + 0.114*b) / 255.0;
				
				boolean isLightBackground = brightness > 0.5;
				String tintContrast = isLightBackground ? "black" : "white";
				String tintSimilar = isLightBackground ? "white" : "black";

				String inj = "#" + CSS.SCIENCE_GADGET_AREA//
						+ "{background-color:#" + mainColor + ";"//
						+ "color:" + tintContrast + ";"//
						+ "}"//
						+ "." + CSS.EQ_PANEL//
						+ "{text-shadow: 0 0 4px " + tintSimilar// "
						+ "}"//
						+ "." + CSS.FRACTION_LINE//
						+ "{border-bottom: 3px solid " + tintContrast + ";"//
						+ "}";//

				StyleInjector.inject(inj);
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}

}
