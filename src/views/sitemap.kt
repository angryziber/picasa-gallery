package views

import photos.Gallery

//language=XML
fun sitemap(host: String, gallery: Gallery) = """
<urlset xmlns="http://www.google.com/schemas/sitemap/0.9">
  <url>
    <loc>https://$host/</loc>
    <lastmod>${gallery.timestampISO}</lastmod>
    <changefreq>weekly</changefreq>
    <priority>1</priority>
  </url>
  ${gallery.albums.values.each { """
    <url>
      <loc>https://$host/$name</loc>
      <lastmod>$timestampISO</lastmod>
      <changefreq>monthly</changefreq>
      <priority>0.8</priority>
    </url>
  """
  }}
</urlset>
"""