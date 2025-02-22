# BMC-logging-json

## Structured JSON formatting support for [Quarkus](https://quarkus.io) logging

## Motivation

There are existing `JsonLogging` libraries in the official and quarkiverse hubs that you can find here:

- [official logging-json](https://quarkus.io/guides/logging#alt-console-format)
- [quarkiverse logging-json](https://docs.quarkiverse.io/quarkus-logging-json/dev/index.html)

I required some extra flexibility that what is currently offered by both existing JSON logging libraries, so I wrote this extension.

## Features

The extra features regarding log JSON formatting are:

### Configuration on the log side:

- Date/time formatting for the log output independent of the client date/time formatting
- Additional fields:
    - can be wrapped in a JSON entry named `additionalFields`
    - can be printed at the top level of the log output
- Using `Map<String,Object>` to render the message field of the log:
    - **NOTE**: there **will** be changes to this approach if a wrapper dataType proves better than a raw `Map<String, Object>`
    - this allows rendering one or many structured objects with a single log call, i.e.:
    ```java
        logger.info("ignored", Map.of("buyer", buyerUser, "seller", sellerUser, "product", soldGoods));
    ```
    - that will print something that can look like:
    ```json
    {
        "message": {
            "buyer": {
                "name": "john",
                "lastName": "doe"
            },
            "seller": {
                "name": "jane",
                "lastName": "smith"
            },
            "product": {
                "type": "beverage",
                "price": 1.50,
                "currency": "USD",
                "amount": 12
            }
        }
    }
    ```

### Configuration on the client object side:

- Convenience temporal serializers:
    - can override the default timestamp formatting by just using `DateTimeFormatter` supported patterns
    - currently supported temporal Classes:
        - `LocalDateTime`
        - `ZonedDateTime`
        - `Instant`
        - `LocalDate`
        - `LocalTime`
- Client serializer injection:
    - custom serializers from client code using this extension can be injected to transform the JSON output of any given object

### Bonus:

**Speed**: Although true for printing any log, usually printing JSON-formated logs hits throughput performance harder than plain logging.

- The approach taken to load this extension into `Quarkus`' `JBOSS LogManager`, makes logging with JSON as fast as standard console logging,
  sometimes even with pretty printing enabled for the same set of fields.
- Speed measurement is anecdotal with innocent repetition of hitting a client application and clocking the output with plain console vs. this
  extension's structured JSON formatting.

> DISCLAIMER: this claim can be erroneous, and I will try to create better measurements, but I have not seen a performance drop by enabling
> this extension compared against other log formatters.

## Missing Features

These features are either missing or have a less mature state that what can be found on the existing libraries:

- Error rendering:
    - currently working to properly render all frames of a full stackTrace in case there is an error to Log as JSON
    - expectation:
        - fully configurable depth / format of error printing in JSON Format
- JSON libraries supports:
    - only [Jackson](https://github.com/FasterXML/jackson/) is supported momentarily as it was the fastest serializer I could find.
    - I will probably not support other serializers for now, given speed and features are the main concern of this extension and `Jackson` ticks all
      boxes so far.

## How this works: the high-level view:

To avoid checking configuration and options that determine the printing of a log record, the approach I took is the following.

Build a template with all configuration options during bootstrap.<br>
Use that template to get the data from the LogRecord.

The practical translation would be:

- you configure to print only five fields.
- you configure to print only year in dates
- you configure to change the `message` field name to `_info_output_`

All that is precomputed at bootstrap time, so when a LogRecord has to be printed, it will only print the chosen fields in the chosen way.<br>
There is no evaluation of active/inactive fields nor evaluation of overridden field names.

If you are interested in how it is actually achieved:

- `Template` file that holds info to print and how to retrieve
  it: -> [StructureLog](runtime/src/main/java/com/bmc/extensions/loggingjson/runtime/models/StructuredLog.java)
- Building process of the
  template: -> [StructuredLogFactory](runtime/src/main/java/com/bmc/extensions/loggingjson/runtime/models/factory/StructuredLogFactory.java)

> All the code is Javadoc'd for easier understanding of the decisions made. There are no Javadocs for straightforward methods.

## Additional Documentation:

For motivation and rationale behind the Extension and teaching / full usage documentation: [FutureDocs]()

## FeatureRequests and Issue Reports:

This extension is in _experimental_ stage.

Once it is stabilized, if there is interest, I will open a feature request / issue channel here on GitHub.

## Will this be published in Quarkiverse Hub?

Once the extension is stabilized, if there is interest, I may migrate it to the quarkus conventions and add it to their hub.

## License

This extension is OSS under Apache License described in the [LICENSE](LICENSE) file
