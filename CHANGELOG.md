## 1.1.6
fix: flight togglesneak transition
- Unsneak immediately when flight starts instead of waiting for another input event
- Previously with the unsneak on flight start option enabled, trying to start flying when toggle sneak was enabled caused a ~1 second delay before when it actually unsneaked

fix: handle flight state transitions correctly
- Emit flight events only when the flying state actually changes. Most events were previously emitted twice
- Call `original.call` inside the `@WrapOperation`