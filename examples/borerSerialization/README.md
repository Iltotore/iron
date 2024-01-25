# Small Demo of borer integration
                                  
This example shows how to integrate _iron_ with [borer](https://sirthias.github.io/borer/).

After having added `"io.github.iltotore" %% "iron-borer" % "<version>"` as a dependency you can say this:

```scala
import io.github.iltotore.iron.borer.given
```

With this import in place all refined types `T` automatically have _borer_ `Encoder[T]` and `Decoder[T]`
instances available, as long as the respective Encoders and Decoders for the (unrefined) underlying types are already
`given`.

If a refinement error is triggered during decoding because the decoded value doesn't match the refinement condition(s)
decoding will fail with a `Borer.Error.ValidationFailure`.


## Run the example

Use the following command (in the Iron project root directory) to run the example:

```sh
mill examples.borerSerialization.run
```
