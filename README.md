# Bank Changes

A RuneLite plugin for Old School RuneScape that tracks how your bank contents
change between visits.

Every time you open your bank, the plugin takes a snapshot of all items (item id
and quantity) and compares it to the snapshot from the previous time you opened
the bank. An overlay then lists what changed:

- **Green** entries (`+N`) for items that increased or were added.
- **Red** entries (`-N`) for items that decreased or were removed.

Snapshots are stored via RuneLite's configuration so they persist across relogs.

## Configuration

- **Show overlay** — toggle the on-screen list of changes.
- **Quantity threshold** — only show changes whose absolute quantity difference
  is at least this value.
- **Max rows** — maximum number of changed items listed in the overlay.
