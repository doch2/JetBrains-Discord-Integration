/*
 * Copyright 2017-2020 Aljoscha Grebe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gamesdk.api.managers

import gamesdk.api.DiscordImageDimensionsResult
import gamesdk.api.DiscordImageHandleResult
import gamesdk.api.DiscordImageHandleResultCallback
import gamesdk.api.DiscordObjectResult
import gamesdk.api.types.DiscordImageHandle
import gamesdk.api.types.DiscordImageSize
import java.awt.image.BufferedImage

public interface ImageManager {
    public fun fetch(handle: DiscordImageHandle, refresh: Boolean, callback: DiscordImageHandleResultCallback)
    public suspend fun fetch(handle: DiscordImageHandle, refresh: Boolean): DiscordImageHandleResult

    public fun getDimensions(handle: DiscordImageHandle): DiscordImageDimensionsResult

    public fun getData(handle: DiscordImageHandle, dataLength: DiscordImageSize): DiscordObjectResult<ByteArray>

    public fun getData(handle: DiscordImageHandle): DiscordObjectResult<ByteArray>

    public suspend fun getImage(handle: DiscordImageHandle): DiscordObjectResult<BufferedImage>
}