# Oblivion
A super lightweight Limbo server made with Minestom

## Config

| Setting               | Note                                                                                    | Default |
|:----------------------|-----------------------------------------------------------------------------------------|---------|
| address               | The address to bind to                                                                  | 0.0.0.0 |   
| port                  | The port to bind to                                                                     | 25565   |
| online-mode           | (currently broken on Minestom 1.19.1 - previous versions work fine)                     | true    |
| proxy                 | Any values from: VELOCITY, BUNGEE, NONE                                                 | NONE    |
| proxy-secret          | If using Velocity, the forwarding secret                                                |         |
| compression-threshold | The max size of a packet before the server attempts to compress it. Set to 0 to disable | 256     |