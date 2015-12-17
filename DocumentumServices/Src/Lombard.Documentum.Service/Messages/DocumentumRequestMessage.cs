namespace Lombard.Documentum.Service.Messages
{
    // Temporary class until we have better domain
    // Currently used only to distinguish different 
    // queues (by type)

    // TODO: messages will probably be defined at higher level than here

    public class DocumentumRequestMessage
    {
        public string Content { get; set; }

        public DocumentumRequestMessage(string content)
        {
            Content = content;
        }
    }
}
