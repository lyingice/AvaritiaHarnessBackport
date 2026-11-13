# Attribution

## Re:Avaritia

The Neutron Harness / 星空挽具 item concept, item texture and entity texture are ported from **Re:Avaritia**, which is licensed under the MIT License.

- Project: https://github.com/Nova-Committee/Re-Avaritia
- License: MIT
- Copyright (c) 2022 cnlimiter

The harness item behavior (CHEST-slot attributes, Protection X, fire resistance, immortal item drop, extreme smithing recipe) is based on Re:Avaritia's `NeutronHarnessItem` implementation on its 1.21.11 / 26.x branches.

The 26.1.2 mechanics (80% happy ghast damage reduction, harness shield, summon mount, +0.45 riding speed) are ported from Re:Avaritia's 26.1.2 branch classes `NeutronHarnessHandler`, `HarnessShieldState`, `HarnessOwner`, `C2SHarnessActionPacket` and `ModDataAttachments`, which are also covered by the MIT license.

## Vanilla Backport

Compatibility is implemented only through public data formats:

- the `minecraft:harnesses` item tag
- the `equipment/ghast` client JSON format
- the registry id `minecraft:happy_ghast`

No code or assets from Vanilla Backport are included. Vanilla Backport is All Rights Reserved.

## Happy Ghast Legacy

Compatibility is implemented only through:

- the registry id `happy_ghast_legacy:happy_ghast`
- the vanilla `EquipmentSlot.CHEST` slot
- the `happy_ghast_legacy:harness` item tag
- a loader-level entity interaction event

The harness render layer/model is an independent implementation of the vanilla happy ghast harness layout; no Happy Ghast Legacy code or assets are included. Happy Ghast Legacy is All Rights Reserved.
