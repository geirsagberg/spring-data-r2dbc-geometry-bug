# Spring Data R2DBC H2 bug

When attempting to select a column of GEOMETRY type, there is an infinite loop:

```
Codec.doDecode(ParameterCodec.java:6) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.AbstractCodec.decode(AbstractCodec.java:60) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.DefaultCodecs.decode(DefaultCodecs.java:57) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:22) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:6) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.AbstractCodec.decode(AbstractCodec.java:60) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.DefaultCodecs.decode(DefaultCodecs.java:57) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:22) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:6) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.AbstractCodec.decode(AbstractCodec.java:60) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.DefaultCodecs.decode(DefaultCodecs.java:57) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:22) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
	at io.r2dbc.h2.codecs.ParameterCodec.doDecode(ParameterCodec.java:6) ~[r2dbc-h2-1.0.0.RELEASE.jar:1.0.0.RELEASE]
```

The issue seems to be:
- For a given column, R2DBC-H2 loops through the given data type and attempts to find a Codec that can decode it.
- `ParameterCodec` return `true` on `canDecode`, i.e. attempts to decode any object.
- In https://github.com/r2dbc/r2dbc-h2/blob/d54de8abb73976cf1780e110eeca6c8a653abad3/src/main/java/io/r2dbc/h2/codecs/DefaultCodecs.java#L146, `ParameterCodec` is added at the end of the list, presumably so any other codecs get priority.
- Since `GeometryCodec` is optionally loaded, after `ParameterCodec`, it is never reached.
- `ParameterCodec` attempts to decode by finding a codec for the parameter object, which in this case will bring it back to find itself because it never reaches `GeometryCodec`.
- A fix could be to insert `ParameterCodec` (and maybe `ArrayCodec`) -after- any optional codecs.
