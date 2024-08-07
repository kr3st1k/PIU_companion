package com.kr3st1k.pumptracker.ui.components.home.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.kr3st1k.pumptracker.core.helpers.Utils.formatString
import com.kr3st1k.pumptracker.core.network.data.User

@Composable
fun UserCard(
    user: User,
    small: Boolean = false,
    hideLocation: Boolean = false,
    showMoney: Boolean = false
)
{
    Card(
        modifier = Modifier
            .width(750.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://www.piugame.com/l_img/bg1.png",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = user.avatarUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .width(70.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = user.titleName,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = Color.White.copy(0.6f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user.username,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                )
                if (!small && !hideLocation)
                    Text(
                        text = user.recentGameAccess,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        color = Color.White.copy(0.7f)
                    )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (user.pumbility != null)
                        Text(
                            text = "${
                                user.pumbility!!.replace(",", "").toInt().formatString()
                            }pp",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.White.copy(0.9f) // or any other color you want
                        )
                    if (!small || showMoney) {
                        Spacer(modifier = Modifier.width(8.dp)) // Add some space between the two texts
                        Text(
                            text = "$${user.coinValue}",
                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                            color = Color.Green.copy(0.9f) // or any other color you want
                        )
                    }
                }
            }
        }
    }
}