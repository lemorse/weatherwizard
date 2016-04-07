# Implementing your own way to find resources #

In shore, you can download faxes and GRIBs from the Internet.
Wherever you don't have Internet access - like at sea - the resources you are interested in to build a Composite will very likely be on your disk.
I had this exact problem during our [2010-2011 trip](http://trip-2010-2011.lediouris.net), the faxes and GRIBs were obtained through [SailMail](http://www.sailmail.com) - and the SSB. Every time I wanted to visualize them in a Composite, I had to go and search the hard disk for the most recent fax of a given type, as many times as there was faxes in my Composite, and same for the GRIB...
Bummer.
That's the kind of thing a computer is supposed to take care of.


## What we have out of the box ##

If you check out the pattern of the **SailMail** directory, you will notice in the dynamic ones that the URLs of the faxes are quite cryptic.
<br />
You would find things like this:
```
  search:chartview.util.SearchUtil.findMostRecentFax(".*500.*", "${fax.path}")
```
You need to notice several things:
  * the prefix (protocol ?) `search:`
  * ` chartview.util.SearchUtil.findMostRecentFax(".*500.*", "${fax.path}") ` that you can identify as a java method in its class, invoked with some parameters.
In the software, there is a Java class named ` chartview.util.SearchUtil `, in which lives the ` findMostRecentFax ` **static** method, that takes two **String** parameters, and returns the location - as a String - of the file containing the expected resource.
<br />
Its signature is
```
    public static String findMostRecentFax(String pattern, String startPath) throws Exception
```
All the methods you want to use with the ` search: ` protocol must be static, take zero or more String(s) as parameters, return a String (containing the location of the file containing the resource), and possibly throwing an Exception.
<br />
To know what methods are available, check the ` chartview.util.SearchUtil ` class in the source section.
<br />
Or you can use javap:
```
  prompt> javap -cp chartadjust.jar chartview.util.SearchUtil
  public class chartview.util.SearchUtil {
  public static final java.lang.String SEARCH_PROTOCOL;
  public chartview.util.SearchUtil();
  public static java.util.List<java.lang.String> findMatchingFiles(java.lang.String, java.lang.String) throws java.lang.Exception;
  public static java.lang.String findMostRecentFile(java.lang.String, java.lang.String) throws java.lang.Exception;
  public static java.lang.String findMostRecentFax(java.lang.String, java.lang.String) throws java.lang.Exception;
  public static java.util.List<java.lang.String> findMatchingSailmailFax(java.lang.String, java.lang.String) throws java.lang.Exception;
  public static java.lang.String dynamicSearch(java.lang.String) throws java.lang.Exception;
  static {};
}
```

## How to implement your own needs ##

_**That is the job of a Java programmer.**_
<br />
If you are not a Java programmer..., maybe you know one. If you don't, then you need to be nice with me, and convince me that your needs will bring some good stuff to the soft ;0) .
<br />
Anyhow, here is the way to proceed.
<br />
**Remember:**
<br />
All the methods you want to use with the ` search: ` protocol must be static, take zero or more String(s) as parameters, return a String (containing the location of the file containing the resource), and possibly throwing an Exception.
<br />
Write your implementation, i.e. ` my.search.stuff.SuperSearch `, and archive it into a jar-file that will be part of the classpath of the program.
<br />
If the method you want to invoke the ` rockingSearch ` method from your pattern, all you need to do is to set the resource URL to
```
  search:my.search.stuff.SuperSearch.rockingSearch("prm.one", "prm.two")
```
... assuming that the method takes 2 parameters.
<br />
Good luck!
<br />
If needed, Java programmers can ping [me](mailto:olivier.lediouris@gmail.com) for details.