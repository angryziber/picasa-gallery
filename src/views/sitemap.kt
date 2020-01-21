package views

import integration.Profile
import photos.Gallery

//language=XML
fun sitemap(host: String, profile: Profile?, gallery: Gallery) = """
<urlset xmlns="http://www.google.com/schemas/sitemap/0.9">
  <url>
    <loc>https://$host/${profile?.slug ?: ""}</loc>
    <lastmod>${gallery.albums.values.first().timestampISO}</lastmod>
    <changefreq>weekly</changefreq>
    <priority>1</priority>
  </url>
  ${gallery.albums.values.each { """
    <url>
      <loc>https://$host$url</loc>
      <lastmod>$timestampISO</lastmod>
      <changefreq>monthly</changefreq>
      <priority>0.8</priority>
    </url>
  """
  }}
</urlset>
"""
