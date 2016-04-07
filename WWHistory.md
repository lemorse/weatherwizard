Weather Wizard History

# Introduction #

This page takes over and replaces the one [Here](http://donpedro.lediouris.net/software/structure/datafiles/news/index.html)


# Details #

| **Date** | **Description** | **Impacted file(s)** |
|:---------|:----------------|:---------------------|
| 03-Mar-2015  | New filter on the comment of a composites, Option NOT to ask if you want to keep the comment when saving, minor tweaks in the console.  | nmeareader.jar, desktop.jar, chartadjust.jar |
| 14-Oct-2014  | Cache age (mostly when using re-broadcasting)  | coreutilities.jar, nmeareader.jar, nmeaparser.jar, desktop.jar |
| 03-Oct-2014  | Introduced a logger (java logger)  | coreutilities.jar, nmeareader.jar, desktop.jar |
| 10-Sep-2014  | Improved NMEA Analyzer, mostly to manage logging aberrations. Also added the performance (polar-based) in the consoles (a graph in the graphical one, and a value in the character one).  | nmeareader.jar, desktop.jar |
| 08-Sep-2014  | NMEA Analyzer and Converter (into GPX). Possibility to define your own NMEA Strings (in config/extra.nmea.properties) | nmeareader.jar, desktop.jar |
| 20-Aug-2014  | Send SPOT requests in Airmail outbox. New preference in the SailFax category. | nmeareader.jar, desktop.jar |
| 26-Jun-2014  | Stored the reg-exp in the Routing from Archives | chartadjust.jar      |
| 26-Jun-2014  | Marquee in the "Live wallpaper" | desktop.jar          |
| 09-Apr-2014  | Replaced "foreground data" with "Live wallpaper" | desktop.jar          |
| 07-Apr-2014  | Weather Wizard now warning when a document is unsaved on exit.<br>NMEA Console: fixed a bug in the HDG-HDM.<br>Desktop: Sun and Moon Altitude and Azimuth now available in the foreground data <table><thead><th> chartadjust.jar, coreutilities.jar, nmeareader.jar </th></thead><tbody>
<tr><td> 26-Mar-2014  </td><td> "SPOT Here" now available in the Weather Wizard. </td><td> chartadjust.jar, coreutilities.jar, nmeareader.jar </td></tr>
<tr><td> 24-Mar-2014  </td><td> SPOT Parser now available in the Desktop. </td><td> desktop.jar, coreutilities.jar, nmeareader.jar </td></tr>
<tr><td> 16-Jan-2014  </td><td> Foreground data in the desktop. Daylight info. </td><td> desktop.jar, tideengineimplementation.jar, almanactools.jar </td></tr>
<tr><td> 23-Dec-2013  </td><td> Satellites in View in the console, and minor details. </td><td> desktop.jar, tideengineimplementation.jar, nmeareader.jar, nmeaparser.jar, chartadjust.jar </td></tr>
<tr><td> 18-Dec-2013  </td><td> Introducing Headless Weather Wizard, see <a href='http://donpedrodalfaroubeira.blogspot.com/2013/12/introducing-headless-weather-wizard.html'>this post</a> </td><td> chartadjust.jar      </td></tr>
<tr><td> 16-Dec-2013  </td><td> Introducing Console User-Exits. </td><td> desktop.jar          </td></tr>
<tr><td> 11-Dec-2013  </td><td> Position acquisition: in the status bar </td><td> chartadjust.jar      </td></tr>
<tr><td> 15-Nov-2013  </td><td> Minor date bug fix, headless console </td><td> nmeaparser.jar, desktop.jar </td></tr>
<tr><td> 08-Nov-2013  </td><td> Improved the image generation (Admin Tools) </td><td> chartadjust.jar      </td></tr>
<tr><td> 06-Nov-2013  </td><td> For Linux systems, in case the file system is not writable, an explicit exception is raised. </td><td> chartadjust.jar      </td></tr>
<tr><td> 25-Oct-2013  </td><td> Fixed bugs in the TCP & UDP re-broadcasting. </td><td> desktop.jar, nmeareader.jar </td></tr>
<tr><td> 17-Oct-2013  </td><td> Fixed a bug in the prate contour detection. </td><td> chartadjust.jar      </td></tr>
<tr><td> 01-Oct-2013  </td><td> pdf publishing on Linux, HTML5/JavaScripts </td><td> nmeareader.jar       </td></tr>
<tr><td> 29-Sep-2013  </td><td> Minor bug fix, and HTML5 first implementation (more to come) </td><td> chartadjust.jar      </td></tr>
<tr><td> 23-Sep-2013  </td><td> Cosmetix        </td><td> chartadjust.jar      </td></tr>
<tr><td> 22-Sep-2013  </td><td> Added VMG & ZMG, plus performance ratio (requires polars) </td><td> nmeareader.jar       </td></tr>
<tr><td> 02-Sep-2013  </td><td> New preference in Display > Others, to set the default font. In some Look & Feels (like Metal), the default font is way too big in the control pane. With this preference, you can choose a font that allows you to see and read whatever is displayed. Default is Arial 12. </td><td> chartadjust.jar      </td></tr>
<tr><td> 28-Aug-2013  </td><td> Added the possibility to get position and time from the GPS, in the Sight Reduction utility. For didactic purpose.</td><td> almanactools.jar     </td></tr>
<tr><td> 14-Aug-2013  </td><td> Cosmetix        </td><td> chartadjust.jar      </td></tr>
<tr><td> 25-Jul-2013  </td><td> Improved Ctrl+T (new tab) and Ctrl+R (Reload default composite). </td><td> chartadjust.jar      </td></tr>
<tr><td> 23-Jul-2013  </td><td> Added progress status when publishing the tide calendar </td><td> tideengineimplementation.jar </td></tr>
<tr><td> 12-Jul-2013  </td><td> New preference: GRIB TWS Coeff. Several people mentioned that the TWS from the GRIBs were underestimated for the North Atlantic and the Mediterranean Sea. There is now a preference - in the Polars, Routing, NMEA category -  that allows you to apply a coefficient to the TWS from the GRIB.<br>This value - if not equal to 1.0 - is displayed in the GRIB Control panel. </td><td> chartadjust.jar      </td></tr>
<tr><td> 08-Jul-2013  </td><td> Added the "Ctrl+R" feature to relodad the default composite.  </td><td> chartadjust.jar      </td></tr>
<tr><td> 05-Jul-2013  </td><td> Added links to the manual in the Weather Wizard.<br> Added the Beaufort scale display on the True Wind Speed, in the NMEA Console  </td><td> chartadjust.jar, coreutilities.jar, nmeareader.jar </td></tr>
<tr><td> 01-Jul-2013  </td><td> Added the "open in a new tab" feature for composites. <br>There is also an HTML5 NMEA console, when data are rebroadcasted using XML over HTTP.  </td><td> chartadjust.jar, tideengineimplementation.jar, desktop.jar </td></tr>
<tr><td> 15-May-2013  </td><td> Fixed a problem in the Weather Wizard position acquisition. </td><td> chartadjust.jar, nmeareader.jar, desktop.jar </td></tr>
<tr><td> 19-Mar-2013  </td><td> Fixed a problem in the night of the Tide app. </td><td> tideengineimplmentation.jar </td></tr>
<tr><td> 11-Mar-2013  </td><td> Added the json over http for the NMEA rebroadcasting. </td><td> desktop.jar, nmeareader.jar </td></tr>
<tr><td> 27-Feb-2013  </td><td> Fixed a minor time zone issue. </td><td> chartadjust.jar      </td></tr>
<tr><td> 25-Feb-2013  </td><td> Added composite archiving feature. </td><td> chartadjust.jar      </td></tr>
<tr><td> 11-Feb-2013  </td><td> GRIB Control Panel: added replay speed for the GRIB animation. Quite useful for the micro-patterns. </td><td> chartadjust.jar      </td></tr>
<tr><td> 07-Feb-2013  </td><td> GRIB Control check box... </td><td> chartadjust.jar      </td></tr>
<tr><td> 23-Jan-2013  </td><td> Composite editor improvement. </td><td> chartadjust.jar      </td></tr>
<tr><td> 22-Jan-2013  </td><td> Improved the radio-group/background features. </td><td> chartadjust.jar      </td></tr>
<tr><td> 17-Jan-2013  </td><td> Fixed a sound bug. </td><td> chartadjust.jar      </td></tr>
<tr><td> 11-Jan-2013  </td><td> Slim down, minor improvements. </td><td> coreutilities.jar, nmeareader.jar </td></tr>
<tr><td> 12-Dec-2012  </td><td> Cosmetic, minor bugs. Blur (color smoothing) for the GRIB display (bottom right check-box). </td><td> chartadjust.jar      </td></tr>
<tr><td> 27-Nov-2012  </td><td> Version 3.0.1.2 released. Added the possibility to draw (free hand) on the chart. </td><td>                      </td></tr>
<tr><td> 7-Nov-2012  </td><td> Micro patterns, ext-resource://, date on the grib display. </td><td> chartadjust.jar      </td></tr>
<tr><td> 5-Oct-2012  </td><td> Bug fixes. Decimal separator for non-English. </td><td> coreutilities.jar, nmeareader.jar, googlelocator.jar, almanactools.jar, desktop.jar </td></tr>
<tr><td> 20-Sep-2012 </td><td> Routing back tracking. Mouse the mouse on the isochrones... </td><td> chartadjust.jar      </td></tr>
<tr><td> 19-Sep-2012 </td><td> Improved the tide engine,<br>improved the routing (avoiding land). </td><td> tideengine.jar, chartadjust.jar </td></tr>
<tr><td> 10-Sep-2012 </td><td> More work on the Mac platform (Thanks to SV Settlement). </td><td> olivsoft.mac         </td></tr>
<tr><td> 2-Aug-2012 </td><td> Fixed the file type option when saving a routing. </td><td> chartadjust.jar      </td></tr>
<tr><td> 27-Jul-2012 </td><td> Added chexboxes in the routing summary. </td><td> chartadjust.jar      </td></tr>
<tr><td> 18-Jul-2012 </td><td> Notifications (bug fix). Use -Dnotifications=all to see all notifications.<br>Added a thumbnail in the control pane. </td><td> chartadjust.jar, coreutilities.jar </td></tr>
<tr><td> 28-Jun-2012 </td><td> New logo in the About box! </td><td> chartadjust.jar, coreutilities.jar </td></tr>
<tr><td> 22-Jun-2012 </td><td> Fixed a bug in the pattern editor </td><td> chartadjust.jar      </td></tr>
<tr><td> 20-Jun-2012 </td><td> Preferences for the shape of the anemometer hand </td><td> chartadjust.jar      </td></tr>
<tr><td> 20-Jun-2012 </td><td> GPDs implementation (beta) </td><td> nmeaparser.jar, nmeareader.jar, chartadjust.jar </td></tr>
<tr><td> 20-May-2012 </td><td> More Cosmetic for the GRIB @ mouse panel, and for the routing details </td><td> chartadjust.jar      </td></tr>
<tr><td> 14-May-2012 </td><td> Cosmetic for the GRIB @ mouse panel </td><td> chartadjust.jar      </td></tr>
<tr><td> 07-May-2012 </td><td> Cosmetic        </td><td> chartadjust.jar      </td></tr>
<tr><td> 30-Apr-2012 </td><td> Dynamic Patterns graphical Editor </td><td> chartadjust.jar, chartcomponents.jar </td></tr>
<tr><td> 18-Apr-2012 </td><td> Cosmetic        </td><td> chartadjust.jar      </td></tr>